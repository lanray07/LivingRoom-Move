import unittest
from translate_drafts import validate

class TranslationContractTests(unittest.TestCase):
    def test_preserves_argument_positions_and_percent_escape(self):
        validate({'x':'%1$d minutes, %2$s, 100%%'}, {'x':'%2$s, %1$d Minuten, 100%%'})
    def test_missing_argument_rejected(self):
        with self.assertRaises(ValueError):
            validate({'x':'%1$d minutes'}, {'x':'minutes'})
    def test_changed_type_rejected(self):
        with self.assertRaises(ValueError):
            validate({'x':'%1$d minutes'}, {'x':'%1$s minutes'})
    def test_extra_missing_or_empty_strings_rejected(self):
        for candidate in ({}, {'x':''}, {'x':'text','y':'extra'}, {'x':123}):
            with self.assertRaises(ValueError):
                validate({'x':'text'}, candidate)

if __name__ == '__main__':
    unittest.main()
