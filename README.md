# testPanoramaGl

by switching panoramas many times You can catch OOM error, because usage of RAM is increased and not cleared when changing panoramas,
i think that problem is in 65 line (when setting panorama to pLManager), because when i remove it there is no OOM error, but panoramas don't show
