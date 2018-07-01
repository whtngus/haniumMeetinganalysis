var LocalStrategy = require('passport-local').Strategy
var bodyParser = require('body-parser');
var bcrypt = require('bcrypt-nodejs');
var mysql = require('mysql');
var conn = mysql.createConnection({
  host     :'hantestdb.cklwl7rg6i5n.ap-northeast-2.rds.amazonaws.com', //데이터베이스가 설치된 서버 url
  user     :'HANJH',      //데이터베이스에 등록한 user
  password :'gksdldma#VIC',      //비밀번호
  database :'hanium'         //만든 데이터베이스 이름
});
conn.connect();

module.exports = function(passport) {

    passport.serializeUser(function(user, done) {
        done(null, user);
    });
    passport.deserializeUser(function(user, done) {
      var sql = 'SELECT * FROM user WHERE userid="yoosoo615"';
      conn.query(sql, user.id, function(err, results){
        if(err){
          console.log(err);
          done('There is no user');
        } else{
          done(null, results[0]);
        }
      })
    });

    passport.use('login', new LocalStrategy({
            usernameField : 'id',
            passwordField : 'password',
            passReqToCallback : true
        },
        function(req, id, password, done) {
          var id = id;
          var pw = password;
          console.log(id, pw);
          var sql = 'SELECT * FROM user WHERE userid=?';
          conn.query(sql, id, function(err, results){
            if(err){
              return done(err);
            }
            var user = results[0];
            console.log(user)
            if (bcrypt.compareSync(pw, user.pw)){
              done(null, user);
            } else{
              done(null, false);
            }
          })
        }));

    passport.use('signup', new LocalStrategy({
        usernameField : 'id',
        passwordField : 'password',
        passReqToCallback : true
    },
    function(req, email, password, done) {
        User.findOne({ 'id' : id }, function(err, user) {
            if (err) return done(err);
            if (user) {
                return done(null, false, req.flash('signupMessage', '이메일이 존재합니다.'));
            } else {
                var newUser = new User();
                newUser.name = req.body.name;
                newUser.email = email;
                newUser.password = newUser.generateHash(password);
                newUser.save(function(err) {
                    if (err)
                        throw err;
                    return done(null, newUser);
                });
            }
        });
    }));
};
