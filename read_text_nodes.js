const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

console.log('Scanning for Gemini shared response elements...');

// Google Bard/Gemini Share has chat content inside a state object.
// Let's look for a large JSON string starting with "[\" or "[[" inside <script> blocks that contains "media" or "haptic" or "visual".
const scriptRegex = /<script[^>]*>([\s\S]*?)<\/script>/gi;
let match;
let count = 0;
while ((match = scriptRegex.exec(html)) !== null) {
    const js = match[1];
    if (js.includes('MusicPage') || js.includes('haptic') || js.includes('waveform') || js.includes('track')) {
        console.log(`Script ${count} contains matches! Length: ${js.length}`);
        // Let's search inside the script for strings containing "haptic" and print them
        const strRegex = /"([^"\\]*(?:\\.[^"\\]*)*)"/g;
        let sMatch;
        let sCount = 0;
        while ((sMatch = strRegex.exec(js)) !== null) {
            const rawStr = sMatch[1];
            // Decode unicode escape sequences (e.g. \u003c, \n, etc.)
            let decodedStr = '';
            try {
                decodedStr = JSON.parse('"' + rawStr + '"');
            } catch (e) {
                decodedStr = rawStr;
            }
            if ((decodedStr.toLowerCase().includes('haptic') || decodedStr.toLowerCase().includes('waveform') || decodedStr.toLowerCase().includes('gesture') || decodedStr.toLowerCase().includes('skip')) && decodedStr.length > 100) {
                console.log(`\n--- FOUND STRING ${sCount} (len: ${decodedStr.length}) ---`);
                console.log(decodedStr.substring(0, 2000));
                sCount++;
            }
        }
    }
    count++;
}
