var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var mysql = require('mysql');
var passport = require('passport');
var session = require('express-session');
var MySQLStore = require('express-mysql-session')(session);

const app = express();
const models = require('./models');

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// session 설정
// mysql 사용
// DB이름: session
app.use(session({
    secret: 'dfd!ek#%@$#kd2343dkf1@$djfo234++',
    resave: false,
    saveUninitialized: true,
    store: new MySQLStore({
      host:'hantestdb.cklwl7rg6i5n.ap-northeast-2.rds.amazonaws.com',
      port:3306,
      user:'HANJH',
      password:'gksdldma#VIC',
      database:'session'
    })
}));

// mysql 데이터베이스 설정
// DB이름: commit
models.sequelize.sync()
  .then(() => {
    console.log('DB connection success.');
  })
  .catch(err => {
    console.error(err);
    process.exit();
  })

// passport 모듈 설정
// user 테이블 사용
app.use(passport.initialize());
app.use(passport.session()); //로그인 세션 유지
require('./config/passport')(passport, models.User);
//플래시메세지를 사용한다면
var flash = require('connect-flash');
app.use(flash());

// 라우터 설정
var loginRouter = require('./routes/login')(passport);
var indexRouter = require('./routes/index');
var meetRouter = require('./routes/meet');
var addRouter = require('./routes/add')(models);
app.use(['/login', '/'], loginRouter);
app.use('/index', indexRouter);
app.use('/meet', meetRouter);
app.use('/add', addRouter);

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
