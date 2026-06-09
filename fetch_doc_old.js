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
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'
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
                fs.writeFileSync('old_doc_content.txt', data);
                console.log(`Successfully written ${data.length} bytes to old_doc_content.txt`);
            });
        } else {
            console.error(`Failed with status code: ${res.statusCode}`);
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

fetchUrl('https://docs.google.com/document/d/1dvbGZiSFM8N3yMuIiils_movPU840D0Q8_319_M96Zg/export?format=txt');
