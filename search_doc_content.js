const fs = require('fs');

if (fs.existsSync('doc_content.txt')) {
    const text = fs.readFileSync('doc_content.txt', 'utf8');
    const lines = text.split('\n');
    console.log("Searching doc_content.txt (total lines:", lines.length, ")");
    
    const keywords = ['music', 'youtube', 'waveform', 'haptic', 'improvement', 'design', 'redo', 'widget', 'layout'];
    keywords.forEach(kw => {
        console.log(`\nMatches for "${kw}":`);
        let matches = 0;
        lines.forEach((line, idx) => {
            if (line.toLowerCase().includes(kw)) {
                if (matches < 8) {
                    console.log(`  Line ${idx + 1}: ${line.trim().substring(0, 160)}`);
                }
                matches++;
            }
        });
        console.log(`Total matches for "${kw}": ${matches}`);
    });
} else {
    console.log("doc_content.txt does not exist!");
}
