var mongoose = require('mongoose');
var User = require('../models/user');

var groupSchema = mongoose.Schema({
    group: [{ id: mongoose.Schema.Types.ObjectId }]
});

groupSchema.virtual('group_detail').get(function() {
  var detail = [];
  var groups = this.group;
  console.log(this);
  console.log(groups);
  return detail;
});

module.exports = mongoose.model('Group', groupSchema, 'users');
