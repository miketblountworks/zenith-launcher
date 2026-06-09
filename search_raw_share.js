const fs = require('fs');

if (fs.existsSync('all_cleaned_strings.txt')) {
    const text = fs.readFileSync('all_cleaned_strings.txt', 'utf8');
    const lines = text.split('\n');
    console.log("Searching all_cleaned_strings.txt (total lines:", lines.length, ")");
    
    // Let's print out lines that look like a user prompt or a conversation response!
    // Often they have specific characters or talk about android launchers or design
    const terms = ['zenith', 'launcher', 'music', 'youtube', 'media', 'widget', 'waveform', 'haptic', 'amplitude', 'volume'];
    
    terms.forEach(t => {
        let count = 0;
        console.log(`\n--- Matches for term: ${t} ---`);
        lines.forEach((line, idx) => {
            if (line.toLowerCase().includes(t)) {
                if (count < 10) {
                    // print context (line-1, line, line+1)
                    console.log(`[Line ${idx + 1}]`);
                    console.log(`  Prev: ${lines[idx-1] ? lines[idx-1].trim().substring(0, 150) : ''}`);
                    console.log(`  Curr: ${line.trim().substring(0, 150)}`);
                    console.log(`  Next: ${lines[idx+1] ? lines[idx+1].trim().substring(0, 150) : ''}`);
                }
                count++;
            }
        });
        console.log(`Total occurrences of "${t}": ${count}`);
    });
} else {
    console.log("all_cleaned_strings.txt not found!");
}
