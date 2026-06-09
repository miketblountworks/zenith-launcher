const fs = require('fs');
const path = require('path');

const keywords = ['launcher', 'zenith', 'screen', 'widget', 'youtube', 'media', 'redo', 'improve', 'home screen'];

console.log("Searching files...");

const files = fs.readdirSync('.').filter(f => f.startsWith('script_') || f.endsWith('.txt') || f.endsWith('.html'));

files.forEach(file => {
    try {
        const content = fs.readFileSync(file, 'utf8');
        const lower = content.toLowerCase();
        
        const found = keywords.filter(kw => lower.includes(kw));
        if (found.length > 2) {
            console.log(`\nFile: ${file} matches keys: ${found.join(', ')}`);
            
            // Find occurrences and print snippet
            const idx = lower.indexOf('zenith');
            if (idx !== -1) {
                console.log(`  Snippet at 'zenith': ...${content.slice(Math.max(0, idx - 100), idx + 200)}...`);
            }
            const idxWidget = lower.indexOf('widget');
            if (idxWidget !== -1) {
                console.log(`  Snippet at 'widget': ...${content.slice(Math.max(0, idxWidget - 100), idxWidget + 200)}...`);
            }
        }
    } catch(err) {
        // ignore
    }
});
