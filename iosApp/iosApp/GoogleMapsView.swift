import SwiftUI
import GoogleMaps

struct GoogleMapsView: UIViewRepresentable {
    let initialCamera: GMSCameraPosition

    func makeUIView(context: Context) -> GMSMapView {
        let camera = initialCamera
        let mapView = GMSMapView()
        mapView.camera = camera

        // marker at the initial position
        let marker = GMSMarker()
        marker.position = camera.target
        marker.title = "Amsterdam"
        marker.snippet = "Current Location"
        marker.map = mapView

        // Configure map UI
        mapView.settings.compassButton = true
        mapView.settings.myLocationButton = true
        mapView.settings.zoomGestures = true
        mapView.settings.scrollGestures = true
        mapView.isMyLocationEnabled = true

        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        // update map view if needed
    }
}

#Preview {
    let camera = GMSCameraPosition(latitude: 52.3702, longitude: 4.8952, zoom: 12)
    GoogleMapsView(initialCamera: camera)
}

