const fs = require('fs');

if (fs.existsSync('long_strings_extracted.txt')) {
    const text = fs.readFileSync('long_strings_extracted.txt', 'utf8');
    console.log("Length of long_strings_extracted.txt:", text.length);
    
    // Print first 1000 characters
    console.log("First 1000 chars:\n", text.substring(0, 1000));
    
    // Search for keywords
    const keywords = ['youtube', 'media', 'control', 'home', 'screen', 'layout', 'design', 'improvement', 'reorder', 'app list', 'drawer', 'widget'];
    keywords.forEach(kw => {
        const regex = new RegExp(kw, 'gi');
        const matches = text.match(regex);
        console.log(`Keyword "${kw}": font ${matches ? matches.length : 0} occurrences`);
    });
} else {
    console.log("long_strings_extracted.txt does not exist!");
}
