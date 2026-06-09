const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

// A share page contains a JSON-like array inside a script tag or global.
// Let's search for deep nested string items that look like paragraphs.
const regex = /"([^"]{40,})"/g;
let match;
const strings = [];

while ((match = regex.exec(html)) !== null) {
    const s = match[1];
    if (s.includes('\\u003c') || s.includes('\\n') || s.includes('Dextera') || s.includes('home') || s.includes('screen') || s.includes('icon') || s.includes('launcher')) {
        // Clean up escaped unicode and HTML
        const clean = s
            .replace(/\\u003cbr \\\/\\u003e/g, '\n')
            .replace(/\\u003c[^>]+>/g, '')
            .replace(/\\n/g, '\n')
            .replace(/\\"/g, '"')
            .replace(/\\u003c/g, '<')
            .replace(/\\u003e/g, '>')
            .replace(/\\u0026/g, '&');
        strings.push(clean);
    }
}

// Remove duplicates and save
const uniqueStrings = [...new Set(strings)];
fs.writeFileSync('extracted_share_content.txt', uniqueStrings.join('\n\n==================================================\n\n'));
console.log(`Extracted ${uniqueStrings.length} potential content blocks to extracted_share_content.txt`);
