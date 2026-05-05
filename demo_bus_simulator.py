import json
import time
import urllib.request
import urllib.error
import sys

# Your correct Asia-Southeast1 Database URL
FIREBASE_URL = "https://vidya-vahini-20c3d-default-rtdb.asia-southeast1.firebasedatabase.app"

def simulate_bus():
    print("🚌 Loading Bus Routes Database...")
    try:
        with open("massive_bmtc_routes_data.json", "r") as f:
            data = json.load(f)
    except Exception as e:
        print("Could not load massive_bmtc_routes_data.json. Make sure the script is running in the correct folder.")
        return

    routes = data.get("routes", {})
    route_ids = list(routes.keys())
    
    print("\n" + "="*50)
    print("   🌟 VIDYA-VAHINI LIVE DEMO SIMULATOR 🌟   ")
    print("="*50)
    print("This script will pretend to be a student sitting on a bus, pinging")
    print("its location as it moves from stop to stop every 5 seconds.")
    print("------------------------------------------------------------------")
    
    print(f"\nPlease enter the EXACT Route ID you registered with in your Android App.")
    print(f"(You can find this by looking at the 'routes' node in your Firebase Console)")
    print(f"Examples: {route_ids[0]}, {route_ids[1]}")
    
    target_route = input("\nEnter Route ID here: ").strip()
    
    if target_route not in routes:
        print("\n❌ Route ID not found in database! Please check exactly what is in your Firebase Console.")
        return
        
    route_data = routes[target_route]
    print(f"\n✅ Target acquired: {route_data['name']}")
    
    stops = route_data.get("stops", {})
    # Sort stops by order (1, 2, 3...)
    sorted_stops = sorted(stops.items(), key=lambda x: x[1]['order'])
    
    print("\n⚠️  IMPORTANT FIREBASE RULES CHANGE REQUIRED ⚠️")
    print("Since this script is not logged in via the Android App, you must temporarily")
    print("allow external writes to the pings node. Go to your Firebase Rules and set:")
    print('''
    "pings": {
      "$routeId": {
        ".read": "auth != null",
        ".write": true   <--- TEMPORARILY CHANGE THIS TO TRUE
      }
    }
    ''')
    input("Press ENTER when you have updated the rules and are ready to start the bus...")
    
    print("\n🚌 Bus is starting its journey...")
    
    for stop_id, stop_info in sorted_stops:
        print(f"\n📍 Arriving at: {stop_info['name']} (Stop {stop_info['order']})")
        
        # This is exactly what the Android app sends when a student taps "Ping"
        ping_data = {
            "stopId": stop_id,
            "timestamp": int(time.time() * 1000),
            "pinggedBy": "demo_simulator_script",
            "status": "on_time"
        }
        
        url = f"{FIREBASE_URL}/pings/{target_route}/latest.json"
        
        # Send a REST PUT request to Firebase
        req = urllib.request.Request(url, data=json.dumps(ping_data).encode('utf-8'), method='PUT')
        req.add_header('Content-Type', 'application/json')
        
        try:
            with urllib.request.urlopen(req) as response:
                pass # success
            print("   -> 📡 Ping successfully sent to Firebase! Look at your Android app map!")
        except urllib.error.HTTPError as e:
            print(f"   ❌ Failed to send ping: {e.code} {e.reason}")
            print("   Did you forget to set \".write\": true in Firebase Rules for the pings node?")
            break
        except Exception as e:
            print(f"   ❌ Unknown Error: {e}")
            break
            
        # Wait 6 seconds before driving to the next stop
        time.sleep(6) 
        
    print("\n🏁 Bus has reached the final destination. Simulation complete!")

if __name__ == "__main__":
    simulate_bus()
