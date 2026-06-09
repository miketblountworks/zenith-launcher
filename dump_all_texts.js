const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

console.log('Searching for visible text in body...');

// Let's find any text inside script tags that starts with JSON data, or can find the shared chat data
// Gemini shares usually serialize data as:
// "c_Records" or as "window.CD_RECORDS" or inside a `<div class="shared-chat-prompt">` or similar things.
// Let's do a case-insensitive search for keywords in the entire file.
const keywords = ['haptic', 'waveform', 'media', 'player', 'duration', 'track', 'canvas'];

// Let's print out portions of the HTML that contain haptic or waveform
const contentLower = html.toLowerCase();
keywords.forEach(kw => {
    let index = 0;
    while (true) {
        index = contentLower.indexOf(kw, index);
        if (index === -1) break;
        console.log(`--- Keyword: ${kw} at position ${index} ---`);
        // Extract 300 characters around the index
        console.log(html.substring(Math.max(0, index - 100), Math.min(html.length, index + 350)));
        index += kw.length;
        if (index > contentLower.length || index > 400000) {
            console.log('... truncated scanning ...');
            break;
        }
    }
});
