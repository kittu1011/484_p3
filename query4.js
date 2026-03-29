// Query 4
// Find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
// Return an array of arrays.

function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);

    let pairs = [];
    // TODO: implement suggest friends
    let males_users = db.users.find({"gender" : "male"});

    males_users.forEach(function(male) {
        let female_users = db.users.find({
            "gender" : "female", 
            "hometown.city" : male.hometown.city,
            "YOB" : {$gt: male.YOB - year_diff, $lt : male.YOB + year_diff}
        });

        female_users.forEach(function(female) {
            if (female.friends.indexOf(male.user_id) === -1 && male.friends.indexOf(female.user_id) === -1) {
                pairs.push([male.user_id,female.user_id]);
            }
        });
    });
    return pairs;
}
