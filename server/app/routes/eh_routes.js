function statusMessage(status, errorCode) {
    return {
        status: status,
        errorCode: errorCode
    }
}
const ErrorMessage = statusMessage.bind(null, "ERROR");

const SUCCESS = statusMessage("SUCCESS", null);
const DB_INSERT_ERROR = ErrorMessage(32);
const DB_FINDING_ERROR = ErrorMessage(33);
const UNKNOWN_TOKEN = ErrorMessage(34);

function sendAnswer(res, obj) {
    res.send(JSON.stringify(obj));
}

function genToken() {
    let token = "";
    for (let i = 0; i < 4; i++) {
        token += Math.random().toString(36).substr(2, 10);
    }
    return token;
}

function makeLog(label) {
    return (msg) => console.log(label + " " + msg);
}

module.exports = function(App, db) {
    let dbo = db.db("test");

    function checkToken(send, obj, func) {
        let log = function (msg) { console.log("checkToken " + msg) };
        dbo.collection("users").findOne({ token : obj.token }, (err, result) => {
            log("checking user");
            if (err) {
                log(err);
                send(DB_FINDING_ERROR);
                return;
            }
            if (result === null) {
                log("unknown token");
                send(UNKNOWN_TOKEN);
                return;
            }
            log("checked user");
            func();
        });
    }
    
    App.post('/signIn', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let user = req.body;
        let log = makeLog("signIn")
        log(user.googleToken);
        dbo.collection("users").findOne({ googleToken: user.googleToken }, (err, result) => {
            if (err) {
                log(err);
                send(DB_FINDING_ERROR);
                return;
            }
            let token, first = false;
            if (result === null) {
                first = true;
                log("adding new user");
                user.token = token = genToken();
                dbo.collection("users").insertOne(user, (err, res) => {
                        if (err) {
                            log(err);
                            send(DB_INSERT_ERROR);
                            return;
                        }
                        log("added new user");
                    });
            }
            else token = result.token;
            send({ token: token, isFirst: first });
        });
    });

    App.post('/addEvent', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let event = req.body;
        let log = makeLog("addEvent");
        event.latitude = Math.floor(event.latitude * 1000000);
        event.longitude = Math.floor(event.longitude * 1000000);
        log(event.name);
        checkToken(send, event, () => {
            dbo.collection("events").insertOne(event, (err) => {
                if (err) {
                    log(err);
                    send(DB_INSERT_ERROR);
                    return;
                }
                send(SUCCESS);
                log("event " + event.name + " added");
            })
        });
    });

    App.post('/getEvents', (req, res) => {
        let send = sendAnswer.bind(null, res);
        let require = req.body;
        let log = makeLog("getEvent");
        log('');
        require.latitude = Math.floor(require.latitude * 1000000);
        require.longitude = Math.floor(require.longitude * 1000000);
        const latitudeUpset = 100000;
        const longitudeUpset = 170000;
        checkToken(send, require, () => {
            let filter = {
                eventDate: { $gte: require.userDate, $lte: require.eventDate },
                cost: { $lte: require.cost },
                latitude: {
                    $gt: require.latitude - latitudeUpset,
                    $lt: require.latitude + latitudeUpset
                },
                longitude: {
                    $gt: require.longitude - longitudeUpset,
                    $lt: require.longitude + longitudeUpset
                }
            };
            if (require.category !== 'Any') filter.category = require.category;
            if (require.subcategory !== 'Any') filter.subcategory = require.subcategory;
            dbo.collection("events").find(filter).toArray((err, result) => {
                if (err) {
                    log(err);
                    send(DB_FINDING_ERROR);
                }
                send({ data : result });
                log("success require");
            });
        });
    });
};