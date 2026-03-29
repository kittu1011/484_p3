// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
    db = db.getSiblingDB(dbname);
    let total_users = 0;
    let count_of_friends = 0;

    db.users.find().forEach(function(user) {
        ++total_users;
        count_of_friends += user.friends.length;
    });
    // TODO: calculate the average friend count
    return count_of_friends / total_users;
}
