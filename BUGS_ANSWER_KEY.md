# Answer key (do not open until you have tested each snippet cold)

| Snippet | Bug | Test that exposes it |
|---|---|---|
| TextUtils.isPalindrome | Not buggy; ambiguity: is an empty string a palindrome? Spec says yes. | `isPalindrome("")` is true |
| TextUtils.isValidPassword | Length check is inverted: `length() > 8` rejects passwords longer than 8 and accepts short ones | `Ab1!` should be invalid but passes; `Abcdefg1!` (9 chars) should be valid but fails |
| BankAccount.withdraw | `amount >= balance` blocks withdrawing the full balance; spec says it is allowed | balance 100, withdraw 100 should leave 0 |
| Paginator.paginate | `items.size() - 1` as the upper bound drops the last element on the final page | 5 items, size 2, page 3 should be `[e]`, returns empty |
| Money | equals overridden without hashCode | `new Money(1.0,"USD")` and `new Money(1.00,"USD")` are equal but hash differently (HashSet test) |
| Playlist | Constructor and getter keep and expose the caller's list (no defensive copy) | Mutate the original list or the returned list, then check playlist contents |
| HitCounter | `count++` is not atomic or synchronized | Many threads incrementing at once; expected total vs actual. Non-deterministic: use a start latch and large counts, and say so in the interview |

## Not bugs, but good clarifying questions
- ShippingCalculator: is "500 or more" free, or "more than 500"? (code treats 500 as free)
- RomanNumerals.toInt: accepts "IIII" and "IC" (non-canonical). Should it?
- TtlCache: at exactly put-time + ttl the entry is expired (code uses `isBefore`)
- EventBus.publish: swallows subscriber exceptions silently
- LruCache.get on a missing key returns null rather than Optional
- CodeGenerator.next can loop forever if the code space is exhausted
