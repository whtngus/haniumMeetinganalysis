module.exports = function(models) { //user정보 받아옴

	var express = require('express');
	var route = express.Router();


	route.get('/', function(req,res) { 
	  res.render('add_friends');
	});

	route.get('/find', async function(req,res) { //async 처리
		var users = await models.User.findAll({
			raw: true,
			attributes: ['userid', 'username', 'comname', 'department','position'],
		});
		console.log(users);
		try{
			var tmp = {"data": []}
			for(var i=0;i<users.length;i++){ //전체 유저 정보 -> Object
				tmp.data.push(Object.values(users[i]));
			}
			console.log(tmp);
			return res.json(tmp); 
		} catch(err) {
			return console.log(err);
		}
	});

	return route;
};