const fs = require('fs');

if (fs.existsSync('app/src/main/java/com/example/MainActivity.kt')) {
    const code = fs.readFileSync('app/src/main/java/com/example/MainActivity.kt', 'utf8');
    const lines = code.split('\n');
    
    console.log("Searching for page/stack references in MainActivity.kt:");
    
    const elements = ['SwipableWidgetStack', 'MusicPage', 'NotificationsPage', 'WidgetPage', 'activePages'];
    
    elements.forEach(element => {
        console.log(`\nMatches for "${element}":`);
        lines.forEach((line, idx) => {
            if (line.includes(element)) {
                console.log(`  Line ${idx + 1}: ${line.trim()}`);
            }
        });
    });
} else {
    console.log("MainActivity.kt not found");
}
