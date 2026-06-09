const https = require('https');
const fs = require('fs');
const url = require('url');

function fetchUrl(targetUrl, redirectCount = 0) {
    if (redirectCount > 5) {
        console.error('Too many redirects');
        process.exit(1);
    }
    console.log(`Fetching: ${targetUrl}`);
    const parsedUrl = url.parse(targetUrl);
    const options = {
        hostname: parsedUrl.hostname,
        path: parsedUrl.path,
        method: 'GET',
        headers: {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.5'
        }
    };

    const req = https.request(options, (res) => {
        console.log(`Status: ${res.statusCode}`);
        if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
            let nextUrl = res.headers.location;
            if (!nextUrl.startsWith('http')) {
                nextUrl = parsedUrl.protocol + '//' + parsedUrl.host + nextUrl;
            }
            fetchUrl(nextUrl, redirectCount + 1);
        } else if (res.statusCode === 200) {
            let data = '';
            res.on('data', (chunk) => { data += chunk; });
            res.on('end', () => {
                fs.writeFileSync('gemini_raw.html', data);
                console.log(`Successfully written ${data.length} bytes to gemini_raw.html`);
                
                // Let's do a simple extraction of text from html
                // Find <title> or any text inside 7ac84274d667
                const matches = data.match(/<title>([\s\S]*?)<\/title>/i);
                if (matches) {
                    console.log('Title:', matches[1]);
                }
            });
        } else {
            console.error(`Failed with status code: ${res.statusCode}`);
            let body = '';
            res.on('data', (chunk) => { body += chunk; });
            res.on('end', () => {
                console.error(`Response body length: ${body.length}`);
            });
        }
    });

    req.on('error', (e) => {
        console.error(`Request error: ${e.message}`);
    });
    
    req.setTimeout(10000, () => {
        console.error('Request timed out');
        req.destroy();
    });

    req.end();
}

fetchUrl('https://gemini.google.com/share/df19a643ef6c');
