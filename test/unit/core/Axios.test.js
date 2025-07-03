const { describe, it, expect, beforeEach, afterEach } = require('@jest/globals');

/**
 * Mock HTTP Client for testing (simulating Axios-like functionality)
 * This represents a hypothetical HTTP client that might be used in the goose project
 */
class MockHttpClient {
  constructor(config = {}) {
    this.defaults = {
      timeout: 5000,
      ...config,
      headers: {
        'Content-Type': 'application/json',
        ...(config.headers || {})
      }
    };
    this.interceptors = {
      request: {
        use: jest.fn(),
        eject: jest.fn(),
        handlers: []
      },
      response: {
        use: jest.fn(),
        eject: jest.fn(),
        handlers: []
      }
    };
  }

  _request(configOrUrl, config) {
    const requestConfig = typeof configOrUrl === 'string' 
      ? { url: configOrUrl, ...config }
      : configOrUrl;

    // Merge with defaults
    const finalConfig = {
      ...this.defaults,
      ...requestConfig,
      headers: {
        ...this.defaults.headers,
        ...(requestConfig.headers || {})
      }
    };

    // Simulate async HTTP request
    return new Promise((resolve, reject) => {
      // Simulate error handling for invalid URLs
      if (!finalConfig.url) {
        const error = new Error('Request URL is required');
        error.config = finalConfig;
        return reject(error);
      }

      // Simulate network error
      if (finalConfig.url.includes('network-error')) {
        const error = new Error('Network Error');
        error.code = 'NETWORK_ERROR';
        error.config = finalConfig;
        return reject(error);
      }

      // Simulate success response
      setTimeout(() => {
        resolve({
          data: { message: 'success' },
          status: 200,
          statusText: 'OK',
          headers: {},
          config: finalConfig
        });
      }, 10);
    });
  }

  request(configOrUrl, config) {
    return this._request(configOrUrl, config);
  }

  get(url, config) {
    return this.request({ ...config, method: 'get', url });
  }

  post(url, data, config) {
    return this.request({ ...config, method: 'post', url, data });
  }

  put(url, data, config) {
    return this.request({ ...config, method: 'put', url, data });
  }

  patch(url, data, config) {
    return this.request({ ...config, method: 'patch', url, data });
  }

  delete(url, config) {
    return this.request({ ...config, method: 'delete', url });
  }
}

describe('core::HttpClient', function() {
  let httpClient;

  beforeEach(() => {
    httpClient = new MockHttpClient();
  });

  describe('error propagation', function() {
    it('should propagate errors with config information', async function() {
      try {
        await httpClient.request({});
        throw new Error('Should have thrown an error');
      } catch (error) {
        expect(error.message).toBe('Request URL is required');
        expect(error.config).toBeDefined();
        expect(error.config.headers).toBeDefined();
      }
    });

    it('should handle network errors gracefully', async function() {
      try {
        await httpClient.request({ url: '/network-error' });
        throw new Error('Should have thrown an error');
      } catch (error) {
        expect(error.code).toBe('NETWORK_ERROR');
        expect(error.config).toBeDefined();
        expect(error.config.url).toBe('/network-error');
      }
    });

    it('should maintain error stack trace', async function() {
      try {
        await httpClient.request({});
      } catch (error) {
        expect(error.stack).toBeDefined();
        expect(typeof error.stack).toBe('string');
      }
    });
  });

  describe('config validation', function() {
    it('should handle null/undefined config values', async function() {
      const response = await httpClient.request({
        url: '/test',
        params: null,
        data: undefined
      });

      expect(response.config.params).toBeNull();
      expect(response.config.data).toBeUndefined();
      expect(response.config.url).toBe('/test');
    });

    it('should properly merge nested config objects', async function() {
      const client = new MockHttpClient({
        headers: {
          'X-Common': 'common',
          'X-Get': 'get-specific'
        }
      });

      const response = await client.request({
        url: '/test',
        headers: {
          'X-Custom': 'custom-value'
        }
      });

      // The MockHttpClient should have merged headers properly
      expect(response.config.headers['X-Common']).toBe('common');
      expect(response.config.headers['X-Custom']).toBe('custom-value');
      expect(response.config.headers['Content-Type']).toBe('application/json');
    });

    it('should validate config before processing', async function() {
      const response = await httpClient.request({
        url: '/test',
        method: 'get',
        timeout: 10000
      });

      expect(response.config.method).toBe('get');
      expect(response.config.timeout).toBe(10000);
      expect(response.config.url).toBe('/test');
    });
  });

  describe('method aliasing', function() {
    it('should support all HTTP method aliases', async function() {
      const testCases = [
        { method: 'get', url: '/get-test' },
        { method: 'post', url: '/post-test', data: { test: 'data' } },
        { method: 'put', url: '/put-test', data: { test: 'data' } },
        { method: 'patch', url: '/patch-test', data: { test: 'data' } },
        { method: 'delete', url: '/delete-test' }
      ];

      for (const testCase of testCases) {
        const { method, url, data } = testCase;
        let response;

        if (data) {
          response = await httpClient[method](url, data);
        } else {
          response = await httpClient[method](url);
        }

        expect(response.config.method).toBe(method);
        expect(response.config.url).toBe(url);
        if (data) {
          expect(response.config.data).toEqual(data);
        }
      }
    });

    it('should handle different data types for POST/PUT/PATCH', async function() {
      const testCases = [
        { method: 'post', data: new FormData() },
        { method: 'put', data: 'string data' },
        { method: 'patch', data: null }
      ];

      for (const { method, data } of testCases) {
        const response = await httpClient[method]('/test', data);
        expect(response.config.data).toBe(data);
        expect(response.config.method).toBe(method);
      }
    });

    it('should maintain consistent response format across methods', async function() {
      const response = await httpClient.get('/test');

      expect(response).toHaveProperty('data');
      expect(response).toHaveProperty('status');
      expect(response).toHaveProperty('statusText');
      expect(response).toHaveProperty('headers');
      expect(response).toHaveProperty('config');
      expect(response.status).toBe(200);
    });
  });

  describe('interceptor management', function() {
    it('should allow registering request interceptors', function() {
      const interceptor = jest.fn();
      httpClient.interceptors.request.use(interceptor);

      expect(httpClient.interceptors.request.use).toHaveBeenCalledWith(interceptor);
    });

    it('should allow registering response interceptors', function() {
      const interceptor = jest.fn();
      httpClient.interceptors.response.use(interceptor);

      expect(httpClient.interceptors.response.use).toHaveBeenCalledWith(interceptor);
    });

    it('should allow ejecting interceptors', function() {
      const interceptorId = 1;
      httpClient.interceptors.request.eject(interceptorId);

      expect(httpClient.interceptors.request.eject).toHaveBeenCalledWith(interceptorId);
    });
  });
});