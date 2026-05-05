import urllib.request
import json
import time

query = """
[out:json][timeout:90];
area["name"="Bengaluru"]->.searchArea;
relation["route"="bus"](area.searchArea);
out body;
>;
out skel qt;
"""

url = "http://overpass-api.de/api/interpreter"
req = urllib.request.Request(url, data=query.encode('utf-8'), headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Vidyavahini/1.0'})

try:
    print("Fetching real BMTC bus route data from OpenStreetMap. This may take a minute...")
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode('utf-8'))
        
    print(f"Fetched {len(data['elements'])} elements from OSM.")
    
    nodes = {}
    routes = {}
    
    # First pass: map node IDs to coordinates
    for el in data['elements']:
        if el['type'] == 'node':
            nodes[el['id']] = {'lat': el['lat'], 'lng': el['lon']}
            
    # Second pass: build routes
    route_count = 0
    for el in data['elements']:
        if el['type'] == 'relation' and 'tags' in el and el['tags'].get('route') == 'bus':
            tags = el['tags']
            ref = tags.get('ref', 'Unknown')
            name = tags.get('name', f"Route {ref}")
            
            # Extract stops from members
            stops = {}
            stop_order = 1
            for member in el.get('members', []):
                if member['type'] == 'node' and member['role'] in ['stop', 'platform', 'stop_entry_only', 'stop_exit_only', '']:
                    node_id = member['ref']
                    if node_id in nodes:
                        stop_name = f"Stop {stop_order}" # OSM doesn't always have stop names attached to relations, but we have coords
                        stops[f"stop_{stop_order:02d}"] = {
                            "name": stop_name,
                            "lat": nodes[node_id]['lat'],
                            "lng": nodes[node_id]['lng'],
                            "order": stop_order
                        }
                        stop_order += 1
            
            if len(stops) > 2:
                route_id = f"route_{route_count}_{ref.replace(' ', '_').lower()}"
                routes[route_id] = {
                    "name": name,
                    "stops": stops
                }
                route_count += 1
                
                if route_count >= 550: # Limit to ~550
                    break

    output = {"routes": routes}
    with open("massive_bmtc_routes_data.json", "w") as f:
        json.dump(output, f, indent=2)
        
    print(f"Successfully generated massive_bmtc_routes_data.json with {len(routes)} real routes!")

except Exception as e:
    print(f"Error fetching data: {e}")
