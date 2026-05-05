import json
import random

HUBS = {
    "Majestic": (12.9766, 77.5713), "Silk Board": (12.9176, 77.6238), "Electronic City": (12.8488, 77.6685),
    "Whitefield": (12.9984, 77.7610), "Hebbal": (13.0354, 77.5988), "Yelahanka": (13.1008, 77.5963),
    "Banashankari": (12.9181, 77.5738), "Jayanagar": (12.9298, 77.5833), "Koramangala": (12.9352, 77.6245),
    "Indiranagar": (12.9784, 77.6408), "KR Puram": (13.0068, 77.6974), "Kengeri": (12.9175, 77.4830),
    "Yeshwanthpur": (13.0285, 77.5409), "Peenya": (13.0329, 77.5274), "Malleswaram": (13.0034, 77.5701),
    "Bellandur": (12.9274, 77.6698), "Marathahalli": (12.9553, 77.6984), "BTM Layout": (12.9165, 77.6101),
    "Shivajinagar": (12.9860, 77.6038), "Domlur": (12.9609, 77.6385), "KIAL Airport": (13.1989, 77.7068),
    "Hoskote": (13.0716, 77.7960), "Sarjapur": (12.8601, 77.7852), "HSR Layout": (12.9121, 77.6446),
    "Jp Nagar": (12.9063, 77.5857), "Vijayanagar": (12.9719, 77.5285), "Rajajinagar": (12.9982, 77.5530),
    "Basavanagudi": (12.9406, 77.5738), "Basaveshwaranagar": (13.0118, 77.5385), "Vidyaranyapura": (13.0780, 77.5562),
    "Bommanahalli": (12.9022, 77.6241), "Madiwala": (12.9226, 77.6174), "Tavarekere": (12.9248, 77.6074),
    "Kalyan Nagar": (13.0280, 77.6399), "Banwadi": (12.8422, 77.5262), "Nayandahalli": (12.9436, 77.5222),
    "RR Nagar": (12.9274, 77.5156), "Tin Factory": (12.9942, 77.6661), "Byappanahalli": (12.9902, 77.6525),
    "Nagarbhavi": (12.9557, 77.5115), "Kammanahalli": (13.0159, 77.6380), "Silk Institute": (12.8715, 77.5350),
    "Nagasandra": (13.0485, 77.4984), "Mysore Road": (12.9482, 77.5312), "Kengeri TTMC": (12.9170, 77.4830),
    "Ullal": (12.9642, 77.4982), "Yelachenahalli": (12.8955, 77.5702), "Nandini Layout": (13.0125, 77.5342)
}

SUFFIXES = ["Cross", "Main", "Gate", "Junction", "Circle", "Bus Stop", "Temple"]

def generate_stops(start_name, start_coords, end_name, end_coords, num_stops):
    stops = {}
    stops[f"stop_01"] = { "name": start_name, "lat": start_coords[0], "lng": start_coords[1], "order": 1 }
    
    for i in range(1, num_stops - 1):
        fraction = i / (num_stops - 1)
        lat = start_coords[0] + (end_coords[0] - start_coords[0]) * fraction + random.uniform(-0.002, 0.002)
        lng = start_coords[1] + (end_coords[1] - start_coords[1]) * fraction + random.uniform(-0.002, 0.002)
        
        # Closest hub to this coordinate
        closest_hub = min(HUBS.keys(), key=lambda h: (HUBS[h][0]-lat)**2 + (HUBS[h][1]-lng)**2)
        suffix = random.choice(SUFFIXES)
        
        stop_name = f"{closest_hub} {suffix}"
        
        # Ensure uniqueness in name slightly
        if i > 1 and stops[f"stop_{i:02d}"]["name"] == stop_name:
            stop_name += f" {random.randint(1,4)}"
            
        stops[f"stop_{i+1:02d}"] = { "name": stop_name, "lat": round(lat, 5), "lng": round(lng, 5), "order": i+1 }
        
    stops[f"stop_{num_stops:02d}"] = { "name": end_name, "lat": end_coords[0], "lng": end_coords[1], "order": num_stops }
    return stops

routes = {}
hubs_list = list(HUBS.items())

prefixes = ["", "V-", "G-", "KIA-", "MF-"]
route_numbers = ["201", "500", "335", "600", "410", "276", "317", "356", "365", "215", "25", "45", "96", "401", "502", "505", "285", "290", "291", "298", "402"]
letters = ["", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "W", "Z"]

for i in range(1, 551):
    start_hub = random.choice(hubs_list)
    end_hub = random.choice(hubs_list)
    while start_hub == end_hub:
        end_hub = random.choice(hubs_list)
        
    route_num = f"{random.choice(prefixes)}{random.choice(route_numbers)}{random.choice(letters)}"
    route_id = f"route_{i}_{route_num.replace('-', '_').lower()}"
    route_name = f"{route_num}: {start_hub[0]} → {end_hub[0]}"
    
    num_stops = random.randint(8, 22)
    stops = generate_stops(start_hub[0], start_hub[1], end_hub[0], end_hub[1], num_stops)
    
    routes[route_id] = {
        "name": route_name,
        "stops": stops
    }

with open("massive_bmtc_routes_data.json", "w") as f:
    json.dump({"routes": routes}, f, indent=2)

print("Generated massive_bmtc_routes_data.json successfully with 550 routes!")
