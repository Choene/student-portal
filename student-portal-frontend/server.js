const express = require('express');
const path = require('path');
const app = express();

// Serve static files from the dist directory
app.use(express.static(__dirname + '/dist/student-portal-frontend/browser'));

// Send all requests to index.html
app.get('/*', function(req, res) {
  res.sendFile(path.join(__dirname + '/dist/student-portal-frontend/browser/index.html'));
});

// Start the app listening on the 8083 port
app.listen(process.env.PORT || 8083);
console.log('Server started!');
