// Query 5
// Find the oldest friend for each user who has a friend. For simplicity,
// use only year of birth to determine age, if there is a tie, use the
// one with smallest user_id. You may find query 2 and query 3 helpful.
// You can create selections if you want. Do not modify users collection.
// Return a javascript object : key is the user_id and the value is the oldest_friend id.
// You should return something like this (order does not matter):
// {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
    db = db.getSiblingDB(dbname);
    db.users.aggregate([
        {$unwind: "$friends"},
        {$project: { user_id: 1, friends: 1, _id: 0} },
        {$out: "flat_users"}
    ]);    // TODO: unwind friends
    let results = {};
    // TODO: implement oldest friends
    db.users.find().forEach(function(user) {
        let user_friends = []
        db.flat_users.find({"friends" : user.user_id}).forEach(function(connection) {
            user_friends.push(connection.user_id);
        })

        db.flat_users.find({"user_id" : user.user_id}).forEach(function(connection) {
            user_friends.push(connection.friends);
        })

        if (user_friends.length == 0) {
            return;
        }

        let oldest = db.users.find({user_id: {$in: user_friends}}).sort({YOB: 1, user_id: 1}).limit(1).next();
        results[user.user_id] = oldest.user_id;
    });
    return results;
}
