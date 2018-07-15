module.exports = function(passport) {

  var express = require('express');
  var route = express.Router();

  route.get('/', (req,res) => {
    res.render('login');
  })
  route.post('/', passport.authenticate('login', {
      successRedirect : '/index',
      failureRedirect : '/login', //로그인 실패시 redirect할 url주소
      failureFlash : true
  }));

  route.get('/signup', (req,res) => {
    res.render('register', { message: req.flash('signup') })
  })

  route.post('/signup', passport.authenticate('signup', {
      successRedirect : '/index',
      failureRedirect : '/signup', //가입 실패시 redirect할 url주소
      failureFlash : true
  }))

  return route;
};
