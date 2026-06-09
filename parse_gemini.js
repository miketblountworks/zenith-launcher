const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

// Gemini shares have conversation details in a script block.
// Usually google shared data shows up in:
// "c_Records" or "CD_RECORDS" or as deep nested arrays inside a <script> block. Let's search for script contents containing 'haptic' or 'media' or similar keywords.
const scripts = [];
const scriptRegex = /<script[^>]*>([\s\S]*?)<\/script>/gi;
let match;
while ((match = scriptRegex.exec(html)) !== null) {
    scripts.push(match[1]);
}

console.log(`Found ${scripts.length} script tags.`);

// Let's search for keywords in the scripts
const keywords = ['haptic', 'waveform', 'media', 'player', 'duration', 'track', 'canvas'];
scripts.forEach((content, i) => {
    const found = keywords.filter(k => content.toLowerCase().includes(k));
    if (found.length > 0) {
        console.log(`Script ${i} (length ${content.length}) contains keywords: ${found.join(', ')}`);
        // Let's write the script content to see what it is
        if (content.length > 1000) {
            fs.writeFileSync(`script_${i}.txt`, content);
            console.log(`Saved script_${i}.txt`);
        }
    }
});

// Also search for any visible text or plain text paragraph inside divs/paragraphs
const textRegex = />([^<]{30,})</g;
const txts = [];
let textMatch;
while ((textMatch = textRegex.exec(html)) !== null) {
    const txt = textMatch[1].trim();
    if (txt && !txt.includes('{\xb2') && !txt.startsWith('(') && !txt.includes('function(')) {
        txts.push(txt);
    }
}
fs.writeFileSync('extracted_texts.txt', txts.join('\n'));
console.log(`Extracted ${txts.length} text fragments to extracted_texts.txt`);
