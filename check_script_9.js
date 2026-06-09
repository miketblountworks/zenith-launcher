const fs = require('fs');

if (fs.existsSync('script_9.txt')) {
    const text = fs.readFileSync('script_9.txt', 'utf8');
    console.log("Length of script_9.txt:", text.length);
    console.log("First 1000 chars:\n", text.substring(0, 1000));
    
    // Check if there is anything readable
    const keywords = ['launcher', 'music', 'screen', 'youtube', 'media'];
    keywords.forEach(kw => {
        const regex = new RegExp(kw, 'gi');
        const matches = text.match(regex);
        console.log(`Keyword "${kw}": ${matches ? matches.length : 0} occurrences`);
    });
} else {
    console.log("script_9.txt does not exist!");
}
