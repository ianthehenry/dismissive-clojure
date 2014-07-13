Dismissive is a "how do I Clojure" project that emails me in the future.

1. Email Dismissive
2. Wait 24 hours
3. Receive an echo of your email

Still to do:

- Allow customization of times in the email (currently hardcoded; needs a good-enough relative time parser)
- Set `reply-to` so that you can snooze messages

All the actual email work is done via Mandrill.
