function sendAnswer(res, status, errorCode) {
    //console.log(JSON.stringify({ status: status, errorCode: errorCode }));
    res.send(JSON.stringify({ status: status, errorCode: errorCode }));
};

function genToken() {
    let token = "";
    for (let i = 0; i < 4; i++) {
        token += Math.random().toString(36).substr(2, 10);
    }
    return token;
}



module.exports = function(App, db) {

    this.myDb = db;
    App.post('/signIn', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let gtoken =  req.body.googleToken;
        console.log("/signIn: " + gtoken);
        let dbo = myDb.db("testdb");
        dbo.collection("users").findOne({ googleToken: gtoken}, (err, result) => {
            if (err) {
                send("ERROR", 33);
                console.log(err);
            }//33 db find error
            else {
                console.log("find element");
                let token, first = false;
                if (result === null) {
                    first = true;
                    dbo.collection("users").insertOne(
                        {
                            googleToken: gtoken,
                            token: token = genToken()
                        }, (err, res) => {
                            if (err) send("ERROR", 32);
                            console.log("insert new element " + token);
                        });
                }
                else token = result.token;
                res.send(JSON.stringify({
                    token: token,
                    isFirst: first
                }));
            }
            //myDb.close();
        });
    });

    App.post('/addEvent', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let dbo = myDb.db("testdb");
        let event = req.body;
        event.latitude = Math.floor(event.latitude * 1000000);
        event.longitude = Math.floor(event.longitude * 1000000);
        console.log("/addEvent " + event.name);
        dbo.collection("users").findOne({ token: event.token }, (err, result) => {
            console.log("checking user")
            if (err) send("ERROR", 33);
            if (result === null) {
                send("ERROR", 34) // unknown token
            }
            else {
                console.log("checked user")
                dbo.collection("events").insertOne(event, (err, res) => {
                    if (err) send("ERROR", 33);
                    console.log("events added " + event.name);
                    send("SUCCESS", null);
                });
            }
        });
    });

    App.post('/getEvents', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let dbo = myDb.db("testdb");
        let event = req.body;
        event.latitude = Math.floor(event.latitude * 1000000);
        event.longitude = Math.floor(event.longitude * 1000000);
        const latitudeUpset = 100000;
        const longitudeUpset = 170000;
        dbo.collection("users").findOne({ token: event.token }, (err, result) => {
            console.log("checking user")
            if (err) send("ERROR", 33);
            if (result === null) {
                send("ERROR", 34) // unknown token
            }
            else {
                console.log("checked user");
                const q1 = event.latitude - latitudeUpset;
                const q2 = event.latitude * 1  + latitudeUpset;
                console.log(q1 + " " + q2);
                dbo.collection("events").find({
                    category: event.category,
                    subcategory: event.subcategory,
                    eventDate: { $gte: event.userDate, $lte: event.eventDate },
                    cost: { $lte: event.cost },
                    latitude: {
                        $gt: event.latitude - latitudeUpset,
                        $lt: event.latitude + latitudeUpset
                    },
                    longitude: {
                        $gt: event.longitude - longitudeUpset,
                        $lt: event.longitude + longitudeUpset
                    }
                }).toArray((err, result) => {
                    if (err) send("ERROR", 33);
                    res.send(JSON.stringify({ data : result }));
                });
            }
        });
    });

    App.post('/signUp', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let user = {
            googleToken: req.body.googleToken,
            firstName: req.body.firstName,
            lastName: req.body.lastName,
            photo: req.body.photo,
            token: genToken()
        };
        console.log("/signUp:" + user.firstName + " " + user.lastName + " "
            + user.googleToken);
        let dbo = myDb.db("testdb");
        console.log(dbo);
        dbo.collection('users').insertOne(user, (err, result) => {
            if (err) {
                console.log("db error: " + err);
                send("ERROR", 32) //32 - db insert error
            } else {
                console.log("success insert in db");
                send("SUCCESS", null);
            }
            myDb.close();
        });
    });
};