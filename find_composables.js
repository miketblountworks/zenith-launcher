const fs = require('fs');

if (fs.existsSync('app/src/main/java/com/example/MainActivity.kt')) {
    const lines = fs.readFileSync('app/src/main/java/com/example/MainActivity.kt', 'utf8').split('\n');
    console.log("Total lines in MainActivity.kt:", lines.length);
    
    let currentInComposable = false;
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        if (line.includes('@Composable')) {
            currentInComposable = true;
        } else if (currentInComposable) {
            if (line.trim().startsWith('fun ')) {
                console.log(`Line ${i + 1}: ${line.trim()}`);
                currentInComposable = false;
            } else if (line.trim().startsWith('inline fun ') || line.trim().startsWith('private fun ')) {
                console.log(`Line ${i + 1}: ${line.trim()}`);
                currentInComposable = false;
            }
        }
    }
} else {
    console.log("MainActivity.kt not found where expected!");
}
