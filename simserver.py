from flask import Flask, jsonify, request
from pymongo import MongoClient
from flask_cors import CORS

app = Flask(__name__)


CORS(app)


client = MongoClient("mongodb+srv://Dvin:<passwordhere>@androidprogramming.ywfra.mongodb.net/?retryWrites=true&w=majority&appName=androidprogramming")

db = client["Sims4"]
collection = db["challenges"]

# Route to fetch all challenges
@app.route('/challenges', methods=['GET'])
def get_challenges():
    challenges = collection.find({}, {"_id": 0})  
    result = []
    
    for challenge in challenges:
        challenge_data = {
            "name": challenge.get("name", ""),
            "generations": []
        }

        # Loop through each generation and append it to the 'generations' field
        for generation in challenge.get("generations", []):
            challenge_data["generations"].append({
                "generationNumber": generation.get("generationNumber", 0),
                "title": generation.get("title", ""),
                "aspiration": generation.get("aspiration", ""),
                "career": generation.get("career", []),
                "goals": generation.get("goals", []),
                "traits": generation.get("traits", [])
            })

        result.append(challenge_data)
    
    return jsonify(result)  

# Route to update a goal's completion status
@app.route('/update_goal', methods=['POST'])
def update_goal():
    data = request.json
    challenge_name = data.get("challengeName")
    generation_number = data.get("generationNumber")
    goal_text = data.get("goalText")
    completed = data.get("completed")
    
    if not challenge_name or not generation_number or goal_text is None or completed is None:
        return jsonify({"error": "Missing required fields"}), 400

    # Find the challenge by name
    challenge = collection.find_one({"name": challenge_name})

    if not challenge:
        return jsonify({"error": "Challenge not found"}), 404

    # Find the generation by number
    generation = next((gen for gen in challenge["generations"] if gen["generationNumber"] == generation_number), None)

    if not generation:
        return jsonify({"error": "Generation not found"}), 404

    # Find the goal and update the 'completed' status
    goal = next((goal for goal in generation["goals"] if goal["text"] == goal_text), None)

    if not goal:
        return jsonify({"error": "Goal not found"}), 404

    # Update the goal's completion status
    goal["completed"] = completed

    # Update the challenge document in MongoDB with the new goal status
    collection.update_one(
        {"name": challenge_name, "generations.generationNumber": generation_number},
        {"$set": {"generations.$.goals": generation["goals"]}}
    )

    return jsonify({"message": "Goal completion updated successfully"}), 200


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)
