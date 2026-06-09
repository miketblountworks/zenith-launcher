const fs = require('fs');

if (!fs.existsSync('script_0.txt')) {
    console.error('script_0.txt does not exist.');
    process.exit(1);
}

const content = fs.readFileSync('script_0.txt', 'utf8');
console.log(`Loaded script_0.txt, length: ${content.length}`);

// Let's write a simple extraction of JSON arrays or blocks
// Let's search for keywords in the file and print 300 characters around them
const keywords = ['haptic', 'waveform', 'media', 'player', 'duration', 'track', 'canvas'];

keywords.forEach(kw => {
    let index = 0;
    while (true) {
        index = content.toLowerCase().indexOf(kw, index);
        if (index === -1) break;
        console.log(`--- Keyword: ${kw} at position ${index} ---`);
        console.log(content.substring(Math.max(0, index - 200), Math.min(content.length, index + 300)));
        index += kw.length;
        if (index > content.length || index > 50000) {  // stop after a limit
            console.log('... truncated scanning ...');
            break; 
        }
    }
});
