const fs = require('fs');

const files = ['all_cleaned_strings.txt', 'extracted_texts.txt', 'long_strings_extracted.txt'];

files.forEach(file => {
    if (fs.existsSync(file)) {
        const text = fs.readFileSync(file, 'utf8');
        console.log(`\nSearching file: ${file} (length: ${text.length})`);
        
        // Find sentences containing key design terms
        const lines = text.split('\n');
        let count = 0;
        lines.forEach((line, idx) => {
            const lower = line.toLowerCase();
            if (lower.includes('home screen') || lower.includes('redo') || lower.includes('design') || lower.includes('layout') || lower.includes('improvement')) {
                if (count < 15) {
                    console.log(`  Line ${idx + 1}: ${line.trim().substring(0, 200)}`);
                    count++;
                }
            }
        });
    }
});
