(function() {
    $(document).ready(function() {
        var movies = new Bloodhound({
          datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
          queryTokenizer: Bloodhound.tokenizers.whitespace,
          remote: {
            url: '/autocomplete?query=%QUERY',
            wildcard: '%QUERY'
          }
        });
        $('#autocomplete').typeahead(null, {
          name: 'movies',
          limit: 10,
          display: 'value',
          hint: true,
          highlight: true,
          source: movies
        });
    });
})();
