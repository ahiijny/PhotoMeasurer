## Photo Measurer

Measures distances and angles.

Signficant digits and reading error is up to the discretion of the user. Keep in mind that most of the trailing decimal places in the measurements are probably worthless.

Uses the metadata-extractor library from https://github.com/drewnoakes/metadata-extractor.

![Example of using the GUI to measure an angle.](screenshot.png?raw=true)

Contains some experimental stuff with colour and wavelengths. Probably not very accurate since colours very rarely consist of a pure single wavelength. Also doesn't incorporate any kind of colour calibration, so colour readings will vary a lot depending on the spectral response of the camera CCD and white balance setting.