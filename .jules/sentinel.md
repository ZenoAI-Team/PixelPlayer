# Sentinel's Security Journal 🛡️

## 2025-05-14 - [Permissive Network Security Config] | Learning: The default network security config was too permissive, allowing cleartext traffic globally and trusting user CAs in all builds. This poses a MitM risk. | Action: Restricted cleartext to localhost/127.0.0.1 and user CA trust to debug builds only.

## 2025-05-14 - [Audio Decoder Resource Leak] | Learning: MediaExtractor and MediaCodec were not being released in case of exceptions during decoding, leading to potential resource exhaustion and crashes. | Action: Wrapped media resource usage in try-finally blocks to ensure release.
