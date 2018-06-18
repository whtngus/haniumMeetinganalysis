var express = require('express');
var route = express.Router();
var User = require('../models/user');

route.get('/info', (req,res) => {
  User.findOne({id: req.user.id},{group: true}).populate('group', '_id name department position').exec((err,data) => {
    if(err){
      console.log(err);
    } else {
      console.log("result: "+ data.group);
      res.render('meet_info', {group: data.group});
    }
  });
});

route.get('/list', (req,res) => {
  User.findOne({id: req.user.id},{meet: true}, (err,data) => {
    if(err){
      console.log(err);
    } else {
      res.render('meet_list', {meet: data.meet});
    }
  });
});

route.post('/process', (req,res) => {
  User.update({id: req.user.id}, {$push: {meet: req.body}}, (err,output) => {
    if(err) {
      console.log(err);
      res.redirect('/fail');
    } else {
      if(!output.n) {
        res.redirect('/fail');
      } else {
        res.render('meet_process');
      }
    }
  })
});

route.post('/book', (req,res) => {
  User.update({id: req.user.id}, {$push: {meet: req.body}}, (err,output) => {
    if(err) {
      console.log(err);
      res.redirect('/fail');
    } else {
      if(!output.n) {
        res.redirect('/fail');
      } else {
        res.redirect('/meet/book/success');
      }
    }
  })
});

route.get('/book/fail', (req,res) => {
  res.send('회의 예약 실패~~');
});

route.get('/book/success', (req,res) => {
  res.render('meet_book_success');
});

module.exports = route;
