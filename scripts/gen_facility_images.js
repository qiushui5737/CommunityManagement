const fs = require('fs');
const path = require('path');

const dir = path.join('e:', '软件综合课程设计', 'IDE', 'CommunityManagement', 'uploads', 'facility');
if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });

const colors = {
  sport:  ['#3b82f6', '#1e40af'],
  tool:   ['#f97316', '#c2410c'],
  ent:    ['#8b5cf6', '#6d28d9'],
  clean:  ['#10b981', '#047857'],
  other:  ['#64748b', '#334155']
};

const items = [
  ['f01.svg', '篮球',       '室内外通用',   'sport'],
  ['f02.svg', '羽毛球拍',   '含球拍+球',    'sport'],
  ['f03.svg', '瑜伽垫',     'TPE防滑',      'sport'],
  ['f04.svg', '跳绳',       '可调节',       'sport'],
  ['f05.svg', '电钻套装',   '博世12V',      'tool'],
  ['f06.svg', '人字梯',     '2米铝合金',    'tool'],
  ['f07.svg', '手推车',     '折叠300kg',    'tool'],
  ['f08.svg', '测距仪',     '激光40m',      'tool'],
  ['f09.svg', '象棋',       '实木棋盘',     'ent'],
  ['f10.svg', '扑克牌',     '2副装',        'ent'],
  ['f11.svg', '投影仪',     '1080P高清',    'ent'],
  ['f12.svg', '高压水枪',   '家用清洗',     'clean'],
  ['f13.svg', '蒸汽清洁机', '高温除菌',     'clean'],
  ['f14.svg', '轮椅',       '折叠标准',     'other'],
  ['f15.svg', '婴儿推车',   '轻便折叠',     'other']
];

for (const [fname, label, sub, cat] of items) {
  const [c1, c2] = colors[cat];
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="${c1}"/><stop offset="100%" stop-color="${c2}"/></linearGradient></defs>
<rect fill="url(#g)" width="400" height="300"/>
<circle cx="330" cy="55" r="65" fill="rgba(255,255,255,0.06)"/>
<circle cx="60" cy="260" r="90" fill="rgba(255,255,255,0.04)"/>
<circle cx="380" cy="240" r="40" fill="rgba(255,255,255,0.05)"/>
<rect x="60" y="80" width="280" height="140" rx="16" fill="rgba(255,255,255,0.12)"/>
<text x="200" y="155" text-anchor="middle" font-size="38" fill="white" font-weight="700" font-family="system-ui,sans-serif">${label}</text>
<text x="200" y="195" text-anchor="middle" font-size="16" fill="rgba(255,255,255,0.7)" font-family="system-ui,sans-serif">${sub}</text>
</svg>`;
  fs.writeFileSync(path.join(dir, fname), svg, 'utf8');
}

console.log(`Generated ${items.length} SVG files in ${dir}`);
fs.readdirSync(dir).forEach(f => console.log(`  ${f}`));
