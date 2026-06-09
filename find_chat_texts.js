const fs = require('fs');

const html = fs.readFileSync('gemini_raw.html', 'utf8');

// Find window.WIZ_global_data = ...
const match = html.match(/window\.WIZ_global_data\s*=\s*({[\s\S]*?});\s*<\/script>/);
if (!match) {
    console.error("Could not find window.WIZ_global_data in html");
    
    // Fallback: search for any massive JSON block or script content containing chat info
    // Let's use a regex to extract any strings matching typical Gemini share content
    const regex = /"([^"\\]*(?:\\.[^"\\]*)*)"/g;
    let m;
    const items = [];
    while ((m = regex.exec(html)) !== null) {
        const val = m[1];
        if (val.length > 200 && (val.includes('Launcher') || val.includes('screen') || val.includes('design') || val.includes('widget'))) {
            items.push(val);
        }
    }
    console.log(`Fallback: found ${items.length} long matching strings.`);
    fs.writeFileSync('chat_debug_fallback.txt', items.join('\n\n---\n\n'));
    process.exit(0);
}

const dataStr = match[1];
try {
    // Note: WIZ_global_data might contain trailing commas or non-strict JSON, but let's try evaluating it
    const data = eval(`(${dataStr})`);
    console.log("Successfully evaluated WIZ_global_data. Keys:", Object.keys(data));
    
    const longStrings = [];
    function traverse(obj) {
        if (!obj) return;
        if (typeof obj === 'string') {
            if (obj.length > 50) {
                longStrings.push(obj);
            }
        } else if (Array.isArray(obj)) {
            obj.forEach(traverse);
        } else if (typeof obj === 'object') {
            Object.values(obj).forEach(traverse);
        }
    }
    traverse(data);
    
    // Sort long strings by length decending and write to a text file
    longStrings.sort((a,b) => b.length - a.length);
    fs.writeFileSync('long_strings_extracted.txt', longStrings.join('\n\n==================================================\n\n'));
    console.log(`Traversed and found ${longStrings.length} long strings. Saved to long_strings_extracted.txt`);
} catch (e) {
    console.error("Failed to parse/eval WIZ_global_data:", e);
    
    // Write just the matched substring for debugging
    fs.writeFileSync('wiz_global_data_raw.txt', dataStr);
}
