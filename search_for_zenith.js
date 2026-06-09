const fs = require('fs');

console.log("Searching for keywords in files...");

const files = ['long_strings_extracted.txt', 'extracted_texts.txt', 'extracted_share_content.txt', 'gemini_raw.html'];

files.forEach(filename => {
    if (!fs.existsSync(filename)) return;
    console.log(`Scanning ${filename}...`);
    const content = fs.readFileSync(filename, 'utf8');
    
    // Search for launcher or zenith or redesign or improve
    const regex = /[^.!?\n]*?(?:launcher|zenith|redesign|improve|home|screen)[^.!?\n]*?[.!?\n]/gi;
    let match;
    let count = 0;
    while ((match = regex.exec(content)) !== null) {
        const sentence = match[0].trim();
        if (sentence.toLowerCase().includes('zenith') || sentence.toLowerCase().includes('improved') || sentence.toLowerCase().includes('redo')) {
            console.log(`  [Match in ${filename}]: ${sentence}`);
            count++;
            if (count > 20) {
                console.log("  ...too many matches, stopping search for this file...");
                break;
            }
        }
    }
});
