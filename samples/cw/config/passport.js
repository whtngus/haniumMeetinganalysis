var LocalStrategy = require('passport-local').Strategy
var bodyParser = require('body-parser');
var bcrypt = require('bcrypt-nodejs');

module.exports = function(passport, User) {

    // 로그인 혹은 회원가입 성공시 실행
    // user 객체를 전달받아 (req.session.passport.user)에 저장함
    passport.serializeUser(function(user, done) {
        done(null, user.id);
    });
    
    // 서버로 들어오는 요청마다 세션정보를 실제 DB와 비교
    passport.deserializeUser(async function(id, done) {
      var user = await User.findById(id);

      if(!user){
        done(null, false);
      } else {
        done(null, user.dataValues);
      }
    });

    // 로그인 전략
    passport.use('login', new LocalStrategy({
            usernameField : 'id',
            passwordField : 'password',
            passReqToCallback : true,
        },
        async function(req, id, password, done) {
          var user = await User.findOne({
            where: {userid: id}
          });

          if(!user){
            return done(null, false, req.flash('logError', '일치하는 계정이 없습니다.'));
          } else {
            if(bcrypt.compareSync(password, user.pw)){
              return done(null, user.dataValues);
            } else {
              return done(null, false, req.flash('logError', '비밀번호가 일치하지 않습니다.'));
            }
          }
        }
      ));

    // 회원가입 전략
    passport.use('signup', new LocalStrategy({
        usernameField : 'id',
        passwordField : 'password',
        passReqToCallback : true, // req 사용함
    },
    async function(req, id, password, done) {

      // 같은 아이디가 있으면 실패
      var user = await User.findOne({
        where: {userid: id}
      });
      if(user && user.length) {
        return done(null, false, req.flash('resError', '이미 존재하는 아이디입니다.'));
      }

      // 새로운 계정 추가
      await User.create({
        userid: id,
        pw: bcrypt.hashSync(password, bcrypt.genSaltSync(8), null),
        username: req.body.name,
        comname: req.body.company,
        department: req.body.department,
        position: req.body.position,
        image: req.body.mypic,
      })
        .then(user => {
          return done(null, user.dataValues);
        })
        .catch(err => {
          return done(null, false, req.flash('resError', '모든 정보를 입력해주세요'));
        });
      }
    ));
};
