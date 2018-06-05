const EhRoutes = require('./eh_routes');
module.exports = function(app, db) {
    EhRoutes(app, db);
    // Тут, позже, будут и другие обработчики маршрутов
};