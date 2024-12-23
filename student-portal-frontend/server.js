const express = require('express');
const path = require('path');
const app = express();

console.log('Current directory:', __dirname);
console.log('Build path:', path.join(__dirname, 'dist/student-portal-frontend/browser'));

app.use(express.static('dist/student-portal-frontend/browser'));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/student-portal-frontend/browser/index.html'));
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
