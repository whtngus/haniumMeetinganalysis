var express = require('express');
var route = express.Router();

route.get('/', (req,res) => {
  if(req.user) {
    console.log(req.user);
    res.render('index', {info: req.user});
  } else {
    res.redirect('/login');
  }
});
route.get('/2', (req,res) => {
  res.render('index2');
});
route.get('/3', (req,res) => {
  res.render('index3');
});

module.exports = route;
