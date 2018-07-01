var LocalStrategy = require('passport-local').Strategy
var bodyParser = require('body-parser');
var User = require('../models/user');
module.exports = function(passport) {

    passport.serializeUser(function(user, done) {
        done(null, user);
    });
    passport.deserializeUser(function(id, done) {
        User.findById(id, function(err, user) {
            done(err, user);
        });
    });
    //프로그램 작성

    passport.use('login', new LocalStrategy({
            usernameField : 'id',
            passwordField : 'password',
            passReqToCallback : true
        },
        function(req, id, password, done) {
            User.findOne({ 'id' : id }, function(err, user) {
                if (err)
                    return done(err);
                if (!user)
                    return done(null, false, req.flash('loginMessage', '사용자를 찾을 수 없습니다.'));
                if (!user.validPassword(password))
                    return done(null, false, req.flash('loginMessage', '비밀번호가 다릅니다.'));
                return done(null, user);
            });
        }));

    passport.use('signup', new LocalStrategy({
        usernameField : 'id',
        passwordField : 'password',
        passReqToCallback : true
    },
    function(req, id, password, done) {
        User.findOne({ 'id' : id }, function(err, user) {
            if (err) return done(err);
            if (user) {
                return done(null, false, req.flash('signupMessage', '이메일이 존재합니다.'));
            } else {
                var newUser = new User();
                newUser.name = req.body.name;
                newUser.id = id;
                newUser.password = newUser.generateHash(password);
                newUser.company = req.body.company;
                newUser.department = req.body.department;
                newUser.position = req.body.position;
                newUser.save(function(err) {
                    if (err)
                        throw err;
                    return done(null, newUser);
                });
            }
        });
    }));
};
