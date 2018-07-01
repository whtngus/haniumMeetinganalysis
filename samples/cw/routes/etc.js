var express = require('express');
var User = require('../models/user');
var route = express.Router();

route.get('/group/add', (req,res) => {
  User.find({},{_id: false, id:true, name:true, company:true, department:true, position:true}, (err,data) => {
    if(err) {
      console.log(err);
    } else {
      var myInfo = {
        id: req.user.id,
        company: req.user.company,
        group: req.user.group
      };
      console.log('group: '+myInfo['group'])
      res.render('group_add',{me: myInfo, users: data});
    }
  })
});

module.exports = route;
