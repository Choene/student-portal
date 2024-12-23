const express = require('express');
const path = require('path');
const app = express();

console.log('Current directory:', __dirname);
console.log('Files in current directory:', require('fs').readdirSync(__dirname));
console.log('Files in dist:', require('fs').readdirSync(path.join(__dirname, 'dist')));

app.use('/', express.static(path.join(__dirname, 'dist/student-portal-frontend/browser')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/student-portal-frontend/browser/index.html'));
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
