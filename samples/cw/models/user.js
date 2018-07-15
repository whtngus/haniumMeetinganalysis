module.exports = function (sequelize, DataTypes) {
  const user = sequelize.define('User', {
    userid: { field: 'userid', type: DataTypes.STRING(45), unique: true, allowNull: false },
    pw: { field: 'pw', type: DataTypes.STRING(225), allowNull: false },
    username: {field: 'username', type: DataTypes.STRING(45), allowNull: false },
    comname: {field: 'comname', type: DataTypes.STRING(255), allowNull: false },
    department: {field: 'department', type: DataTypes.STRING(125), allowNull: false },
    position: {field: 'position', type: DataTypes.STRING(45), allowNull: false },
    image: {field: 'image', type: DataTypes.BLOB, allowNull: true },
  }, {
    // don't use camelcase for automatically added attributes but underscore style
    // so updatedAt will be updated_at
    underscored: true,

    // disable the modification of tablenames; By default, sequelize will automatically
    // transform all passed model names (first parameter of define) into plural.
    // if you don't want that, set the following
    freezeTableName: true,

    // define the table's name
    tableName: 'user'
  });

  return user;
};
