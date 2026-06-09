const fs = require('fs');

if (!fs.existsSync('long_strings.txt')) {
    console.error('long_strings.txt does not exist.');
    process.exit(1);
}

const content = fs.readFileSync('long_strings.txt', 'utf8');
const blocks = content.split('\n\n---\n\n');
console.log(`Loaded long_strings.txt, split into ${blocks.length} blocks.`);

blocks.forEach((block, idx) => {
    console.log(`\n================ BLOCK ${idx} (len: ${block.length}) ================`);
    console.log(block.substring(0, 1500));
    if (block.length > 1500) {
        console.log('... [TRUNCATED] ...');
    }
});
