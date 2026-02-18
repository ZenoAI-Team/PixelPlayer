# PixelPlayer: Void-Iconic Edition 🎵

<p align="center">
  <img src="assets/icon.png" alt="App Icon" width="128"/>
</p>

<p align="center">
  <strong>The definitive high-fidelity, AI-augmented local music platform for Android.</strong><br>
  Merging audiophile-grade DSP precision with Gemini-powered intelligent automation.
</p>

<p align="center">
  <img src="assets/screenshot1.jpg" alt="Screenshot 1" width="200" style="border-radius:26px;"/>
  <img src="assets/screenshot2.jpg" alt="Screenshot 2" width="200" style="border-radius:26px;"/>
  <img src="assets/screenshot3.jpg" alt="Screenshot 3" width="200" style="border-radius:26px;"/>
  <img src="assets/screenshot4.jpg" alt="Screenshot 4" width="200" style="border-radius:26px;"/>
</p>

<p align="center">
    <a href="https://github.com/theovilardo/PixelPlayer/releases/latest">
        <img src="https://img.shields.io/github/v/release/theovilardo/PixelPlayer?include_prereleases&logo=github&style=for-the-badge&label=Latest%20Release" alt="Latest Release">
    </a>
    <a href="https://github.com/theovilardo/PixelPlayer/releases">
        <img src="https://img.shields.io/github/downloads/theovilardo/PixelPlayer/total?logo=github&style=for-the-badge" alt="Total Downloads">
    </a>
    <img src="https://img.shields.io/badge/Android-11%2B-green?style=for-the-badge&logo=android" alt="Android 11+">
    <img src="https://img.shields.io/badge/Kotlin-100%25-purple?style=for-the-badge&logo=kotlin" alt="Kotlin">
</p>

---

## 🏛️ Project Vision & Leadership
**Lead Architect & Project Visionary:** [VoidX3D](https://github.com/voidx3d)

The **Void-Iconic Edition** represents a complete technical and visual overhaul of PixelPlayer, transforming it from a modern open-source player into an elite Android audio platform.

---

## 🧱 Technical Architecture: The Three Pillars

### 1. Titan High-Fidelity Audio Engine (Module I)
The core of Void-Iconic is the **Titan Engine**, designed for absolute sonic transparency and precision.
- **32-Band Parametric EQ:** Total control over frequency, gain, and Q-factor.
- **32-bit Internal Processing:** Minimal quantization error and maximum headroom.
- **Bit-Perfect Mode:** Bypasses Android's system resampler for native DAC playback (FLAC, DSD, MQA).
- **Pro Gain Management:** ReplayGain integration and a ±30dB Pre-Amp stage.
- **Per-Device DSP Profiles:** Automatic preset switching based on Bluetooth MAC or wired ID.

### 2. Void-Intelligence (Module II)
Advanced AI integration powered by Gemini 2.0 that moves beyond simple playback into autonomous curation.
- **AI Magic Wand Interface:** Natural language processing for complex queue and playlist operations.
- **Autonomous Metadata Engine:** Background workers for 4K album art and synchronized lyrics.
- **Pixel Wrapped:** On-device analytics engine with listening heatmaps and shareable cards.
- **Smart Search:** Full-text search (FTS5) across lyrics, bitrate, and custom tags.

### 3. Iconic Visual Identity (Module III)
A signature aesthetic that matches the technical excellence of the engine.
- **Void-Neon Theme:** True AMOLED blacks with high-contrast neon accents.
- **Glassmorphism Engine:** GPU-optimized real-time blur for an immersive "Now Playing" experience.
- **Reactive Waveform Scrubber:** Frequency-reactive visual seeking powered by Amplituda.

---

## ✨ Feature Comparison

| Feature | Standard Edition | Void-Iconic Edition |
|---------|------------------|---------------------|
| **Audio Processing** | 10-Band EQ | 32-Band Parametric EQ |
| **Bit Depth** | 16/24-bit | 32-bit Internal Processing |
| **DAC Support** | Standard Android | Bit-Perfect Exclusive Mode |
| **AI Integration** | Basic Daily Mix | Gemini 2.0 Magic Wand |
| **Metadata** | Manual/Auto | Autonomous Background Engine |
| **Theming** | Material You | Void-Neon Signature Theme |
| **Analytics** | Basic Stats | Pixel Wrapped Heatmaps |

---

## 🛠️ Roadmap: The Evolution

### Phase I: Identity Foundation (Current)
- [x] VoidX3D Vision Integration
- [x] About Screen & Documentation Overhaul
- [x] Void-Iconic Branding Establishment

### Phase II: Titan Engine
- [ ] 32-Band Parametric EQ Core
- [ ] 32-bit Audio Path Implementation
- [ ] Bit-Perfect USB DAC Mode
- [ ] Per-Device DSP Profiles & ReplayGain

### Phase III: Intelligence Layer
- [ ] Gemini 2.0 Magic Wand Interface
- [ ] WorkManager AI Metadata Workers (Deezer/LRCLIB)
- [ ] Smart Search (FTS5) & Listening Heatmaps

### Phase IV: Visual & Experience Layer
- [ ] Glassmorphism Engine & AMOLED Protection
- [ ] Reactive Waveform Scrubber
- [ ] Void-Neon Theme Presets & Studio Mode

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | [Kotlin](https://kotlinlang.org/) 100% |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Audio Engine** | Titan (Media3 ExoPlayer + FFmpeg) |
| **AI Engine** | Void-Intelligence (Gemini 2.0) |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | [Hilt](https://dagger.dev/hilt/) |
| **Database** | [Room (FTS5)](https://developer.android.com/training/data-storage/room) |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1+
- Android SDK 30+
- JDK 17+

### Installation
1. Clone the repository: `git clone https://github.com/theovilardo/PixelPlayer.git`
2. Open in Android Studio.
3. Sync Gradle and build the `:app` module.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
<p align="center">
  Lead Architect & Project Visionary: <strong>VoidX3D</strong><br>
  Built upon the foundation by <strong>theovilardo</strong>
</p>
