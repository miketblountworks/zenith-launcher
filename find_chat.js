const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

console.log('Searching for conversation messages in gemini_raw.html...');

// Let's search for "can we redo the media" or "redo the media player" or "research paper"
// or search for strings matching common patterns.
const lines = html.split('\n');
lines.forEach((line, i) => {
    if (line.toLowerCase().includes('media') && line.length > 500) {
        console.log(`Line ${i} (length ${line.length}) contains 'media'`);
        // Let's write a snippet around the word 'media' in this line
        const idx = line.toLowerCase().indexOf('media');
        console.log('Snippet:', line.substring(Math.max(0, idx - 200), Math.min(line.length, idx + 800)));
    }
});

// Let's write a regex that matches JSON arrays and looks for text that contains 'haptic' or 'media' or similar things.
const regex = /"([^"]{100,})"/g;
let match;
const foundTexts = [];
while ((match = regex.exec(html)) !== null) {
    const text = match[1];
    if (text.includes('media') || text.includes('haptic') || text.includes('waveform') || text.includes('player') || text.includes('gesture')) {
        foundTexts.push(text);
    }
}
console.log(`Found ${foundTexts.length} long strings with keywords.`);
fs.writeFileSync('long_strings.txt', foundTexts.join('\n\n---\n\n'));
console.log('Saved long_strings.txt');
