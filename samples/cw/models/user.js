var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');
var userSchema = mongoose.Schema({
    name: String,
    id : String,
    password : String,
    company: String,
    department: String,
    position: String,
    group: [{type: mongoose.Schema.Types.ObjectId, ref: 'User'}],
    meet : [{
      subject: String,
      when: Date,
      where: String,
      who: [],
      who_detail: [],
      note: String
    }]
});
//password를 암호화
userSchema.methods.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};
//password의 유효성 검증
userSchema.methods.validPassword = function(password) {
    return bcrypt.compareSync(password, this.password);
};

module.exports = mongoose.model('User', userSchema);
