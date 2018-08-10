(function() {
    'use strict';

    angular
        .module('acmeApp')
        .factory('QuestionnaireSearch', QuestionnaireSearch);

    QuestionnaireSearch.$inject = ['$resource'];

    function QuestionnaireSearch($resource) {
        var resourceUrl =  'api/_search/questionnaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
