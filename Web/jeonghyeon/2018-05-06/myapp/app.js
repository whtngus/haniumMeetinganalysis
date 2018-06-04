var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var mongoose = require('mongoose');
var passport = require('passport');
var session = require('express-session');
var MongoStore = require('connect-mongo')(session);

var app = express();

mongoose.connect('mongodb://localhost:27017/test');
mongoose.Promise = global.Promise;
var db = mongoose.connection;
//데이터베이스 접속 확인
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function (callback) {
    console.log("mongo DB connected...")
});

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
    secret: 'dfd!ek#%@$#kd2343dkf1@$djfo234++',
    resave: false,
    saveUninitialized: true,
    store: new MongoStore({
    url:"mongodb://localhost:27017/test",
    ttl: 60*60*24*7  // 7 days (default: 14days)
  })
}));

require('./config/passport')(passport);
app.use(passport.initialize());
app.use(passport.session()); //로그인 세션 유지
//플래시메세지를 사용한다면
var flash = require('connect-flash');
app.use(flash());

var loginRouter = require('./routes/login')(passport);
var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
app.use(['/login', '/'], loginRouter);
app.use('/index', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
