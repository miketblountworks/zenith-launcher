const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

// Regex for tag contents or JSON string matching to find actual text
const strings = [];

// 1. Extract from standard double-quoted JSON strings
const doubleQuoteRegex = /"([^"\\]*(?:\\.[^"\\]*)*)"/g;
let match;
while ((match = doubleQuoteRegex.exec(html)) !== null) {
    const s = match[1];
    if (s.length > 80) {
        strings.push(s);
    }
}

// 2. Extract from single-quoted strings
const singleQuoteRegex = /'([^'\\]*(?:\\.[^'\\]*)*)'/g;
while ((match = singleQuoteRegex.exec(html)) !== null) {
    const s = match[1];
    if (s.length > 80) {
        strings.push(s);
    }
}

// 3. Extract text nodes using a simple HTML text parser
const tagTextRegex = />([^<]{40,})</g;
while ((match = tagTextRegex.exec(html)) !== null) {
    strings.push(match[1]);
}

// Clean and decode strings
const decodedStrings = [];
strings.forEach(s => {
    let dec = s;
    try {
        dec = JSON.parse('"' + s.replace(/\\"/g, '"').replace(/"/g, '\\"') + '"');
    } catch(e) {}
    
    // Clean up html entities and formatting tags
    dec = dec
        .replace(/\\u003cbr \\\/\\u003e/g, '\n')
        .replace(/\\u003cp>/g, '')
        .replace(/\\u003c\/p>/g, '\n')
        .replace(/<[^>]+>/g, '')
        .replace(/\\n/g, '\n')
        .replace(/\\t/g, '\t')
        .replace(/&amp;/g, '&')
        .replace(/&quot;/g, '"')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .trim();
        
    if (dec.length > 60 && !dec.includes('function(') && !dec.includes('var ') && !dec.includes('@media') && !dec.includes('.gb_')) {
        decodedStrings.push(dec);
    }
});

const unique = [...new Set(decodedStrings)];
// Sort by length to read the longest ones first (usually the chat message content!)
unique.sort((a, b) => b.length - a.length);

fs.writeFileSync('all_cleaned_strings.txt', unique.join('\n\n==================================================\n\n'));
console.log(`Extracted ${unique.length} clean strings to all_cleaned_strings.txt`);

// Print top 15 longest strings
for (let i = 0; i < Math.min(15, unique.length); i++) {
    console.log(`\n--- STRING ${i} (len: ${unique[i].length}) ---`);
    console.log(unique[i].substring(0, 350) + "...");
}
