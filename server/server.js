const express      = require('express');
const MongoClient  = require('mongodb').MongoClient;
const bodyParser   = require('body-parser');
const app          = express();
const db           = require('./config/db');

const port = 8000;

app.use(bodyParser.json({})); // imit - default value 100kb, value in bytes
require('./app/routes')(app, {});
MongoClient.connect(db.url, { useNewUrlParser: true } , (err, db) => {
    if (err) return console.log(err + "shit");
    require('./app/routes')(app, db);
    app.listen(port, () => {
        console.log('We are live on ' + port);
    });
});
